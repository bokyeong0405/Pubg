import React, { useState, useEffect } from 'react';
import { useParams, useSearchParams, useNavigate } from 'react-router-dom';
import { getMatchStats } from '../services/api';
import {
  Container,
  Typography,
  CircularProgress,
  Alert,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Box,
  Button
} from '@mui/material';

const MatchPage = () => {
  const { matchId } = useParams();
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const nickname = searchParams.get('nickname');
  const [matchStats, setMatchStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchMatchStats = async () => {
      try {
        setLoading(true);
        const response = await getMatchStats(matchId);
        // Group by teamId
        const groupedByTeam = response.data.reduce((acc, player) => {
          const teamId = player.teamId || 'unknown';
          if (!acc[teamId]) {
            acc[teamId] = [];
          }
          acc[teamId].push(player);
          return acc;
        }, {});

        // Sort players within each team
        for (const teamId in groupedByTeam) {
          groupedByTeam[teamId].sort((a, b) => {
            if (a.winPlace !== b.winPlace) {
              return a.winPlace - b.winPlace;
            }
            return b.kills - a.kills;
          });
        }

        setMatchStats(groupedByTeam);
      } catch (err) {
        setError('매치 정보를 불러오는데 실패했습니다.');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchMatchStats();
  }, [matchId]);

  if (loading) {
    return <CircularProgress sx={{ display: 'block', margin: '100px auto' }} />;
  }

  if (error) {
    return <Alert severity="error">{error}</Alert>;
  }

  if (!matchStats) {
    return <Alert severity="info">매치 데이터가 없습니다.</Alert>;
  }

  return (
    <Container maxWidth="lg">
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={4}>
        <Typography variant="h4" component="h1">
          매치 상세 정보
        </Typography>
        <Button variant="outlined" onClick={() => navigate(`/players/${nickname}`)}>
          플레이어 정보로 돌아가기
        </Button>
      </Box>
      {Object.entries(matchStats)
        .sort(([, playersA], [, playersB]) => {
          const bestRankA = Math.min(...playersA.map(p => p.winPlace));
          const bestRankB = Math.min(...playersB.map(p => p.winPlace));
          return bestRankA - bestRankB;
        })
        .map(([teamId, players]) => (
        <Box key={teamId} mb={5}>
          <Typography variant="h6" component="h2" gutterBottom>
            팀 {teamId}
          </Typography>
          <TableContainer component={Paper}>
            <Table sx={{ minWidth: 650 }} aria-label="simple table">
              <TableHead>
                <TableRow>
                  <TableCell>플레이어</TableCell>
                  <TableCell align="right">Kills</TableCell>
                  <TableCell align="right">Assists</TableCell>
                  <TableCell align="right">Damage</TableCell>
                  <TableCell align="right">DBNOs</TableCell>
                  <TableCell align="right">생존 시간</TableCell>
                  <TableCell align="right">등수</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {players.map((player) => (
                  <TableRow
                    key={player.name}
                    sx={{
                      '&:last-child td, &:last-child th': { border: 0 },
                      backgroundColor: player.name.toLowerCase() === nickname?.toLowerCase() ? 'primary.dark' : 'inherit'
                    }}
                  >
                    <TableCell component="th" scope="row">
                      {player.name}
                    </TableCell>
                    <TableCell align="right">{player.kills}</TableCell>
                    <TableCell align="right">{player.assists}</TableCell>
                    <TableCell align="right">{Math.round(player.damageDealt)}</TableCell>
                    <TableCell align="right">{player.dbnos}</TableCell>
                    <TableCell align="right">{Math.round(player.timeSurvived / 60)}분</TableCell>
                    <TableCell align="right">{player.winPlace}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </Box>
      ))}
    </Container>
  );
};

export default MatchPage;
