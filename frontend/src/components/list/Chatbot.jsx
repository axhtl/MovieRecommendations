import React, { useState } from 'react';
import '../styles/Chatbot.css'; // 필요한 스타일 파일 추가

const Chatbot = ({ isOpen, onClose }) => {
  const [messages, setMessages] = useState([]); // 대화 내역
  const [input, setInput] = useState(''); // 입력된 메시지
  const userName = '사용자'; // 사용자 이름
  const aiName = 'AI'; // AI 이름
  const userProfile = 'https://via.placeholder.com/40?text=U'; // 사용자 프로필 이미지
  const aiProfile = 'https://via.placeholder.com/40?text=AI'; // AI 프로필 이미지

  // 메시지 전송 처리 (AI 응답 활성화)
  const handleSend = async () => {
    if (!input.trim()) return; // 빈 메시지 처리

    const timestamp = new Date().toLocaleTimeString(); // 현재 시간
    const userMessage = {
      sender: 'user',
      text: input,
      time: timestamp,
      name: userName,
      profile: userProfile,
    };

    // 사용자 메시지 추가
    setMessages((prev) => [...prev, userMessage]);

    try {
      // API 요청 보내기
      const response = await fetch('http://127.0.0.1:8086/llm', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ text: input }),
      });

      if (!response.ok) {
        throw new Error('네트워크 응답에 문제가 있습니다.');
      }

      const data = await response.json();
      const aiMessage = {
        sender: 'ai',
        text: data.llm_response,
        time: new Date().toLocaleTimeString(),
        name: aiName,
        profile: aiProfile,
      };

      // AI 메시지 추가
      setMessages((prev) => [...prev, aiMessage]);
    } catch (error) {
      console.error('AI 응답 처리 중 오류 발생:', error);

      // 에러 메시지 추가
      const errorMessage = {
        sender: 'ai',
        text: 'AI 응답 처리에 실패했습니다. 나중에 다시 시도해 주세요.',
        time: new Date().toLocaleTimeString(),
        name: aiName,
        profile: aiProfile,
      };

      setMessages((prev) => [...prev, errorMessage]);
    }

    setInput(''); // 입력 초기화
  };

  if (!isOpen) return null; // 창이 닫혀있으면 렌더링하지 않음

  return (
    <div className="chat-window">
      <div className="chat-header">
        <h4>챗봇</h4>
        <button onClick={onClose} className="chat-close-button">×</button>
      </div>
      <div className="chat-messages">
        {messages.map((msg, index) => (
          <div
            key={index}
            className={`chat-message ${
              msg.sender === 'user' ? 'user-message' : 'bot-message'
            }`}
          >
            <img
              src={msg.profile}
              alt={`${msg.name} profile`}
              className="chat-profile"
            />
            <div>
              <div className="chat-message-header">
                {msg.name} • {msg.time}
              </div>
              <div className="chat-message-content">{msg.text}</div>
            </div>
          </div>
        ))}
      </div>
      <div className="chat-input">
        <input
          type="text"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          placeholder="메시지를 입력하세요..."
        />
        <button onClick={handleSend}>전송</button>
      </div>
    </div>
  );
};

export default Chatbot;
