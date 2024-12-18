import React, { useState } from 'react';
import '../styles/Chatbot.css';

const Chatbot = ({ isOpen, onClose }) => {
  const [messages, setMessages] = useState([]); // 대화 내역
  const [input, setInput] = useState(''); // 입력된 메시지
  const userName = '사용자'; // 사용자 이름
  const aiName = 'AI'; // AI 이름
  const userProfile = 'https://via.placeholder.com/40?text=U'; // 사용자 프로필 이미지
  const aiProfile = 'https://via.placeholder.com/40?text=AI'; // AI 프로필 이미지

  // 메시지 전송 처리
  const handleSend = async () => {
    if (!input.trim()) {
      alert('메시지를 입력해주세요.');
      return;
    }

    const timestamp = new Date().toLocaleTimeString(); // 현재 시간
    const userMessage = {
      sender: 'user',
      text: input,
      time: timestamp,
      name: userName,
      profile: userProfile,
    };

    setMessages((prev) => [...prev, userMessage]);

    try {
      const token = localStorage.getItem('token'); // 토큰 가져오기
      if (!token) {
        throw new Error('로그인이 필요합니다. 토큰이 없습니다.');
      }

      const response = await fetch('/api/ai/chatbot', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`, // 토큰 추가
        },
        body: JSON.stringify({ text: input }), // 요청 본문에 입력된 텍스트 추가
      });

      if (!response.ok) {
        throw new Error(`HTTP 상태 코드: ${response.status}`);
      }

      const data = await response.json();

      const aiMessage = {
        sender: 'ai',
        text: data[0]?.llm_response || data.llm_response || 'AI 응답 없음',
        time: new Date().toLocaleTimeString(),
        name: aiName,
        profile: aiProfile,
      };

      setMessages((prev) => [...prev, aiMessage]);
    } catch (error) {
      console.error('AI 응답 처리 중 오류 발생:', error);

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
