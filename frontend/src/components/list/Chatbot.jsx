import React, { useState } from 'react';
import '../styles/Chatbot.css'; // 필요한 스타일 파일 추가

const Chatbot = ({ isOpen, onClose }) => {
  const [messages, setMessages] = useState([]); // 대화 내역
  const [input, setInput] = useState(''); // 입력된 메시지
  const userName = '사용자'; // 사용자 이름
  const aiName = 'AI'; // AI 이름
  const userProfile = 'https://via.placeholder.com/40?text=U'; // 사용자 프로필 이미지
  const aiProfile = 'https://via.placeholder.com/40?text=AI'; // AI 프로필 이미지

  // 메시지 전송 처리 (더미 메시지 추가)
  const handleSend = () => {
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

    // 더미 AI 응답 추가
    const aiMessage = {
      sender: 'ai',
      text: '현재 AI 응답이 비활성화되었습니다.',
      time: new Date().toLocaleTimeString(),
      name: aiName,
      profile: aiProfile,
    };

    setMessages((prev) => [...prev, aiMessage]); // AI 메시지 추가
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
