import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import '../styles/Edit.css';

const Edit = () => {
  const { memberId } = useParams(); // URL에서 memberId 가져오기
  const navigate = useNavigate();
  const [memberData, setMemberData] = useState({
    password: '',
    nickname: '',
    gender: '',
    age: '',
    preferredGenres: [],
    preferredActors: [],
  });
  const [newGenre, setNewGenre] = useState('');
  const [newActor, setNewActor] = useState('');

  useEffect(() => {
    // 회원 정보 불러오기
    const fetchMemberData = async () => {
      try {
        const response = await axios.get(`/survey/${memberId}`); // 설문조사 정보 API 호출
        setMemberData(response.data);
      } catch (error) {
        console.error('회원 정보 불러오기 오류:', error);
      }
    };

    fetchMemberData();
  }, [memberId]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setMemberData((prevData) => ({
      ...prevData,
      [name]: value,
    }));
  };

  const updateStateAfterChange = (field, value) => {
    setMemberData((prevData) => ({
      ...prevData,
      [field]: value,
    }));
  };

  const handlePasswordChange = async () => {
    try {
      await axios.put(`/member/password/${memberId}`, { password: memberData.password });
      alert('비밀번호가 성공적으로 수정되었습니다.');
    } catch (error) {
      console.error('비밀번호 수정 오류:', error);
      alert('비밀번호 수정에 실패했습니다.');
    }
  };

  const handleNicknameChange = async () => {
    try {
      await axios.put(`/member/nickname/${memberId}`, { nickname: memberData.nickname });
      alert('닉네임이 성공적으로 수정되었습니다.');
    } catch (error) {
      console.error('닉네임 수정 오류:', error);
      alert('닉네임 수정에 실패했습니다.');
    }
  };

  const handleWithdraw = async () => {
    try {
      await axios.put(`/member/withdraw/${memberId}`);
      alert('회원 탈퇴가 완료되었습니다.');
      navigate(`/`); // 홈 페이지로 이동
    } catch (error) {
      console.error('회원 탈퇴 오류:', error);
      alert('회원 탈퇴에 실패했습니다.');
    }
  };

  const handleGenderChange = async () => {
    try {
      const response = await axios.patch(`/survey/${memberId}/gender`, { gender: memberData.gender });
      alert('성별이 성공적으로 수정되었습니다.');
      setMemberData((prevData) => ({
        ...prevData,
        gender: response.data.gender, // 서버 응답 데이터를 상태에 반영
      }));
    } catch (error) {
      console.error('성별 수정 오류:', error);
      alert('성별 수정에 실패했습니다.');
    }
  };
  
  const handleAgeChange = async () => {
    try {
      await axios.patch(`/survey/${memberId}/age`, { age: memberData.age });
      alert('나이가 성공적으로 수정되었습니다.');
      updateStateAfterChange('age', memberData.age);
    } catch (error) {
      console.error('나이 수정 오류:', error);
      alert('나이 수정에 실패했습니다.');
    }
  };

  const handleAddGenre = async () => {
    if (!newGenre.trim()) {
      alert('장르를 입력하세요.');
      return;
    }
    try {
      const response = await axios.post(`/survey/${memberId}/genre`, { genre: newGenre });
      alert('선호 장르가 성공적으로 추가되었습니다.');
      setNewGenre('');
      setMemberData((prevData) => ({
        ...prevData,
        preferredGenres: [...prevData.preferredGenres, response.data],
      }));
    } catch (error) {
      console.error('선호 장르 추가 오류:', error);
      alert('선호 장르 추가에 실패했습니다.');
    }
  };

  const handleDeleteGenre = async (genre) => {
    try {
      await axios.delete(`/survey/preferred-genres/${genre}`);
      alert('선호 장르가 성공적으로 삭제되었습니다.');
      setMemberData((prevData) => ({
        ...prevData,
        preferredGenres: prevData.preferredGenres.filter((g) => g !== genre),
      }));
    } catch (error) {
      console.error('선호 장르 삭제 오류:', error);
      alert('선호 장르 삭제에 실패했습니다.');
    }
  };

  const handleAddActor = async () => {
    if (!newActor.trim()) {
      alert('배우를 입력하세요.');
      return;
    }
    try {
      const response = await axios.post(`/survey/${memberId}/actor`, { actor: newActor });
      alert('선호 배우가 성공적으로 추가되었습니다.');
      setNewActor('');
      setMemberData((prevData) => ({
        ...prevData,
        preferredActors: [...prevData.preferredActors, response.data],
      }));
    } catch (error) {
      console.error('선호 배우 추가 오류:', error);
      alert('선호 배우 추가에 실패했습니다.');
    }
  };

  const handleDeleteActor = async (actor) => {
    try {
      await axios.delete(`/survey/preferred-actors/${actor}`);
      alert('선호 배우가 성공적으로 삭제되었습니다.');
      setMemberData((prevData) => ({
        ...prevData,
        preferredActors: prevData.preferredActors.filter((a) => a !== actor),
      }));
    } catch (error) {
      console.error('선호 배우 삭제 오류:', error);
      alert('선호 배우 삭제에 실패했습니다.');
    }
  };

  return (
    <div className="edit-page">
      <h2>회원 정보 수정</h2>
      <form className="edit-form">
        <label>
          비밀번호:
          <input
            type="password"
            name="password"
            value={memberData.password}
            onChange={handleChange}
          />
          <button type="button" onClick={handlePasswordChange} className="save-button">수정</button>
        </label>
        <label>
          닉네임:
          <input
            type="text"
            name="nickname"
            value={memberData.nickname}
            onChange={handleChange}
          />
          <button type="button" onClick={handleNicknameChange} className="save-button">수정</button>
        </label>
        <label>
          성별:
          <select name="gender" value={memberData.gender} onChange={handleChange}>
            <option value="">선택</option>
            <option value="M">남성</option>
            <option value="F">여성</option>
          </select>
          <button type="button" onClick={handleGenderChange} className="save-button">수정</button>
        </label>
        <label>
          나이:
          <input
            type="number"
            name="age"
            value={memberData.age}
            onChange={handleChange}
          />
          <button type="button" onClick={handleAgeChange} className="save-button">수정</button>
        </label>
      </form>

      <div className="genre-section">
        <h3>선호 장르 관리</h3>
        <input
          type="text"
          value={newGenre}
          onChange={(e) => setNewGenre(e.target.value)}
          placeholder="새로운 장르 입력"
        />
        <button type="button" onClick={handleAddGenre} className="save-button">추가</button>
        <ul>
          {memberData.preferredGenres.map((genre) => (
            <li key={genre}>
              {genre} <button onClick={() => handleDeleteGenre(genre)}>삭제</button>
            </li>
          ))}
        </ul>
      </div>

      <div className="actor-section">
        <h3>선호 배우 관리</h3>
        <input
          type="text"
          value={newActor}
          onChange={(e) => setNewActor(e.target.value)}
          placeholder="새로운 배우 입력"
        />
        <button type="button" onClick={handleAddActor} className="save-button">추가</button>
        <ul>
          {memberData.preferredActors.map((actor) => (
            <li key={actor}>
              {actor} <button onClick={() => handleDeleteActor(actor)}>삭제</button>
            </li>
          ))}
        </ul>
      </div>

      <button type="button" onClick={handleWithdraw} className="withdraw-button">회원 탈퇴</button>
    </div>
  );
};

export default Edit;
