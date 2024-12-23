import json
import time
from datasets import Dataset
from transformers import AutoTokenizer, AutoModelForCausalLM, TrainingArguments, Trainer, DataCollatorForSeq2Seq
from peft import LoraConfig, LoraRuntimeConfig, get_peft_model

# 1. 데이터 로드 함수
def load_text_data(file_path):
    with open(file_path, "r", encoding="utf-8") as f:
        raw_text = f.read().split("---")
    data = []
    for block in raw_text:
        lines = block.strip().split("\n")
        if len(lines) < 4:
            continue
        try:
            title = lines[0].split(": ", 1)[1]
            overview = lines[1].split(": ", 1)[1] if "Overview" in lines[1] else ""
            data.append({
                "prompt": f"Provide details about the movie titled '{title}'.",
                "completion": overview
            })
        except IndexError:
            print(f"Skipped malformed block: {block}")
            continue
    return Dataset.from_list(data)

# 2. 데이터 로드 및 분리
file_path = "/home/andra123438/all_movies.txt"
dataset = load_text_data(file_path)

def filter_short_or_empty(dataset, min_length=10):
    return dataset.filter(lambda x: len(x["completion"].strip()) >= min_length)

dataset = filter_short_or_empty(dataset)

dataset = dataset.train_test_split(test_size=0.1)

print("Final Dataset Loaded:")
print(f"Number of rows in train: {len(dataset['train'])}")
print(f"Number of rows in test: {len(dataset['test'])}")


# Train 데이터만 줄이기
percentage = 1
small_train_dataset = dataset["train"].select(range(int(percentage * len(dataset["train"]))))

# Train과 Test 데이터셋을 분리하여 관리
dataset = {
    "train": small_train_dataset,
    "test": dataset["test"]  # 테스트 데이터는 원본 유지
}

# 3. 모델 및 Tokenizer 로드
model_name = "AcrylaLLM/Llama-3-8B-Jonathan-aLLM-Instruct-v1.0"
tokenizer = AutoTokenizer.from_pretrained(model_name)
model = AutoModelForCausalLM.from_pretrained(model_name)

# 4. 데이터셋 전처리
# def preprocess_data(example):
#     inputs = example["prompt"]
#     outputs = example["completion"]
    
#     # 모델 입력과 출력 시퀀스 생성
#     model_inputs = tokenizer(inputs, max_length=512, truncation=True, padding="max_length")
#     labels = tokenizer(outputs, max_length=512, truncation=True, padding="max_length")
    
#     model_inputs["labels"] = labels["input_ids"]
#     return model_inputs

def preprocess_data(example):
    inputs = example["prompt"]
    outputs = example["completion"]
    
    # 입력과 출력의 토큰화
    model_inputs = tokenizer(inputs, max_length=512, truncation=True, padding="max_length")
    labels = tokenizer(outputs, max_length=512, truncation=True, padding="max_length")
    
    # 레이블에서 패딩 토큰 무시 (-100 사용)
    labels["input_ids"] = [
        (label if label != tokenizer.pad_token_id else -100) for label in labels["input_ids"]
    ]
    
    # 입력과 출력의 길이 일치 강제
    input_len = len(model_inputs["input_ids"])
    label_len = len(labels["input_ids"])
    if input_len != label_len:
        min_len = min(input_len, label_len)
        model_inputs["input_ids"] = model_inputs["input_ids"][:min_len]
        model_inputs["attention_mask"] = model_inputs["attention_mask"][:min_len]
        labels["input_ids"] = labels["input_ids"][:min_len]
    
    model_inputs["labels"] = labels["input_ids"]
    return model_inputs



# 데이터셋 변환 (패딩 포함)
# 데이터 전처리
tokenized_train_dataset = small_train_dataset.map(
    preprocess_data, batched=True, remove_columns=["prompt", "completion"], num_proc=4
)

tokenized_test_dataset = dataset["test"].map(
    preprocess_data, batched=True, remove_columns=["prompt", "completion"]
)

# 전처리된 데이터셋을 딕셔너리 형태로 다시 구성
tokenized_dataset = {
    "train": tokenized_train_dataset,
    "test": tokenized_test_dataset
}



# 5. LoRA(DoRA) 설정
runtime_config = LoraRuntimeConfig(ephemeral_gpu_offload=True)
config = LoraConfig(
    use_dora=False,
    runtime_config=runtime_config,
    r=4,
    lora_alpha=16,
    target_modules=["q_proj", "v_proj"],
    lora_dropout=0.0,
    bias="none"
)

model = get_peft_model(model, config)

# 6. 학습 설정
training_args = TrainingArguments(
    output_dir="./results100_lora",
    eval_strategy="steps",  # 기존 evaluation_strategy 대신 eval_strategy 사용
    save_strategy="steps",
    eval_steps=3000,
    logging_steps=100,
    save_steps=3000,
    learning_rate=2e-4,
    per_device_train_batch_size=8,
    per_device_eval_batch_size=8,
    num_train_epochs=1,
    weight_decay=0.01,
    push_to_hub=False,
    report_to="none",
    fp16=True,
    remove_unused_columns=True,
)

data_collator = DataCollatorForSeq2Seq(
    tokenizer=tokenizer,
    model=model,
    padding="max_length"  # 배치 단위로 패딩 처리 True
)

# 7. Trainer 설정
trainer = Trainer(
    model=model,
    args=training_args,
    train_dataset=tokenized_dataset["train"],
    eval_dataset=tokenized_dataset["test"],
    data_collator=data_collator  # 패딩을 처리하는 Data Collator 설정
)

# 8. 파인튜닝 실행
trainer.train()

# 9. 모델 저장
model.save_pretrained("./finetuned_model_lora")
tokenizer.save_pretrained("./finetuned_model_lora")
