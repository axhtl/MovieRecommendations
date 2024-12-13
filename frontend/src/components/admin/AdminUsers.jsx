import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import AdminNavbar from '../ui/AdminNavbar';
import axios from 'axios';
import '../styles/admin/AdminUsers.css';

function AdminUsers() {
  const [users, setUsers] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    const memberId = localStorage.getItem('memberId');
    const token = localStorage.getItem('token');

    if (!memberId || memberId !== '1' || !token) {
      alert('접근 권한이 없습니다.');
      navigate('/');
    }

    const fetchUsers = async () => {
      try {
        const response = await axios.get('/member/users', {
          headers: { Authorization: `Bearer ${token}` },
        });
        setUsers(response.data);
      } catch (error) {
        console.error('사용자 정보를 가져오는 중 오류 발생:', error);
        alert('사용자 정보를 가져올 수 없습니다.');
      }
    };

    fetchUsers();
  }, [navigate]);

  // 회원 정지 기능
  const suspendMember = async (memberId) => {
    const token = localStorage.getItem('token');
    if (window.confirm(`회원 ID ${memberId}를 정지하시겠습니까?`)) {
      try {
        const response = await axios.put(
          `/members/${memberId}/suspend`,
          {},
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );
        alert(response.data || '회원이 성공적으로 정지되었습니다.');

        // 정지 후 사용자 상태 업데이트
        setUsers((prevUsers) =>
          prevUsers.map((user) =>
            user.member.memberId === memberId
              ? { ...user, member: { ...user.member, memberStatus: 'SUSPENDED' } }
              : user
          )
        );
      } catch (error) {
        console.error('회원 정지 중 오류 발생:', error);
        alert('회원 정지에 실패했습니다.');
      }
    }
  };

  return (
    <div className="admin-page">
      <AdminNavbar />
      <div className="admin-content">
        <h1>회원 관리</h1>
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Member Name</th>
              <th>Nickname</th>
              <th>Role</th>
              <th>Member Status</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {users.map((user, index) => (
              <tr key={index}>
                <td>{user.member.memberId}</td>
                <td>{user.member.membername}</td>
                <td>{user.member.nickname}</td>
                <td>{user.member.role}</td>
                <td>{user.member.memberStatus}</td>
                <td>
                  <button
                    className="action-button"
                    onClick={() => suspendMember(user.member.memberId)}
                  >
                    정지
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default AdminUsers;
