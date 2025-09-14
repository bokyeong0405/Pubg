import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { TextField, Button, Box } from '@mui/material';
import { styled } from '@mui/system';

const StyledTextField = styled(TextField)({
  '& .MuiFilledInput-root': {
    backgroundColor: 'rgba(255, 255, 255, 0.1)',
    color: 'white',
    '&:hover': {
      backgroundColor: 'rgba(255, 255, 255, 0.15)',
    },
    '&.Mui-focused': {
      backgroundColor: 'rgba(255, 255, 255, 0.15)',
    },
  },
  '& .MuiInputLabel-root': {
    color: '#9e9e9e',
  },
  '& .MuiInputLabel-root.Mui-focused': {
    color: 'white',
  },
});

const SearchBar = () => {
  const [nickname, setNickname] = useState('');
  const navigate = useNavigate();

  const handleSearch = () => {
    if (nickname.trim()) {
      navigate(`/players/${nickname.trim()}`);
    }
  };

  const handleKeyPress = (event) => {
    if (event.key === 'Enter') {
      handleSearch();
    }
  };

  return (
    <Box display="flex" justifyContent="center" alignItems="center" my={4}>
      <StyledTextField
        label="PUBG 닉네임을 입력하세요"
        variant="filled"
        value={nickname}
        onChange={(e) => setNickname(e.target.value)}
        onKeyPress={handleKeyPress}
        style={{ width: '400px' }}
      />
      <Button
        variant="contained"
        onClick={handleSearch}
        sx={{ 
          marginLeft: '10px', 
          height: '56px', 
          backgroundColor: '#f57c00', 
          '&:hover': { backgroundColor: '#e65100' } 
        }}
      >
        검색
      </Button>
    </Box>
  );
};

export default SearchBar;
