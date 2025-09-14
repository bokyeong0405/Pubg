import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { searchPlayer } from '../services/api';
import {
  Container,
  Typography,
  CircularProgress,
  Alert,
  Grid,
  Card,
  CardContent,
  CardActions,
  Button,
  Box,
  Pagination
} from '@mui/material';

const PlayerPage = () => {
  const { nickname } = useParams();
  const navigate = useNavigate();
  const [playerData, setPlayerData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [page, setPage] = useState(1);

  useEffect(() => {
    const fetchPlayerData = async () => {
      try {
        setLoading(true);
        const response = await searchPlayer(nickname, page - 1); // API page is 0-indexed
        setPlayerData(response.data);
      } catch (err) {
        setError('플레이어 정보를 불러오는데 실패했습니다.');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchPlayerData();
  }, [nickname, page]);

  const handleMatchClick = (matchId) => {
    navigate(`/matches/${matchId}?nickname=${nickname}`);
  };

  const handlePageChange = (event, value) => {
    setPage(value);
  };

  if (loading) {
    return <CircularProgress sx={{ display: 'block', margin: '100px auto' }} />;
  }

  if (error) {
    return <Alert severity="error">{error}</Alert>;
  }

  if (!playerData) {
    return <Alert severity="info">플레이어 데이터가 없습니다.</Alert>;
  }

  const { matchPage } = playerData;

  return (
    <Container maxWidth="lg">
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={4}>
        <Typography variant="h4" component="h1">
          {nickname}
        </Typography>
        <Button variant="outlined" onClick={() => navigate('/')}>
          다른 닉네임 검색
        </Button>
      </Box>
      <Typography variant="h6" component="h2" gutterBottom>
        최근 매치 기록
      </Typography>
      <Grid container spacing={3}>
        {matchPage.content.map((match) => (
          <Grid item xs={12} sm={6} md={4} key={match.matchId}>
            <Card>
              <CardContent>
                <Typography variant="h5" component="div">
                  {match.gameMode}
                </Typography>
                <Typography sx={{ mb: 1.5 }} color="text.secondary">
                  #{match.winPlace}
                </Typography>
                <Grid container spacing={1}>
                  <Grid item xs={6}>
                    <Typography variant="body2">Kills: {match.kills}</Typography>
                  </Grid>
                  <Grid item xs={6}>
                    <Typography variant="body2">Damage: {Math.round(match.damageDealt)}</Typography>
                  </Grid>
                  <Grid item xs={6}>
                    <Typography variant="body2">Assists: {match.assists}</Typography>
                  </Grid>
                  <Grid item xs={6}>
                    <Typography variant="body2">DBNOs: {match.dbnos}</Typography>
                  </Grid>
                </Grid>
              </CardContent>
              <CardActions>
                <Button size="small" onClick={() => handleMatchClick(match.matchId)}>자세히 보기</Button>
              </CardActions>
            </Card>
          </Grid>
        ))}
      </Grid>
      <Box display="flex" justifyContent="center" mt={4}>
        <Pagination
          count={matchPage.totalPages}
          page={page}
          onChange={handlePageChange}
          color="primary"
        />
      </Box>
    </Container>
  );
};

export default PlayerPage;
