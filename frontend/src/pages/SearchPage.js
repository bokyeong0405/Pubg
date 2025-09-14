import React from 'react';
import SearchBar from '../components/SearchBar';
import { Typography, Container } from '@mui/material';

const SearchPage = () => {
  return (
    <Container maxWidth="md" sx={{ textAlign: 'center', pt: 8 }}>
      <Typography variant="h2" component="h1" gutterBottom>
        PUBG 전적 검색
      </Typography>
      <Typography variant="h5" component="p" color="text.secondary" sx={{ mb: 4 }}>
        플레이어의 닉네임을 검색하여 전적을 확인하세요.
      </Typography>
      <SearchBar />
    </Container>
  );
};

export default SearchPage;
