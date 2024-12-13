import axios from 'axios';

// Flask 서버 URL 설정
const API_BASE_URL = '/llm';

/**
 * Flask 서버에 사용자 입력을 보내고 응답을 받는 함수
 * @param {string} text - 사용자 입력
 * @returns {Promise<string>} - AI 서버의 응답
 */

export const sendTextToLLM = async (text) => {
  try {
    const response = await axios.post(API_BASE_URL, { text });
    return response.data.llm_response; // 서버에서 반환한 'llm_response'
  } catch (error) {
    console.error('AI 서버 요청 오류:', error);
    throw error;
  }
};
