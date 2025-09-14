import axios from 'axios';

const apiClient = axios.create({
  baseURL: 'http://localhost:8080/api/pubg',
  headers: {
    'Content-Type': 'application/json',
  },
});

export const searchPlayer = (nickname, page = 0) => {
  return apiClient.get(`/search`, { params: { nickname, page } });
};

export const getMatchStats = (matchId) => {
  return apiClient.get(`/matches/${matchId}`);
};
