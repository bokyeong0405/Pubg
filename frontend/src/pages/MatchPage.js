import React, { useState, useEffect } from 'react';
import { useParams, useSearchParams, useNavigate } from 'react-router-dom';
import { getMatchStats, getMatchAnalysis } from '../services/api';
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
  Button,
  Card,
  CardContent,
  CardHeader
} from '@mui/material';

const MatchPage = () => {
  const { matchId } = useParams();
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const nickname = searchParams.get('nickname');
  const [matchStats, setMatchStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [analysis, setAnalysis] = useState('');
  const [loadingAnalysis, setLoadingAnalysis] = useState(false);

  useEffect(() => {
    const fetchMatchStats = async () => {
      try {
        setLoading(true);
        const response = await getMatchStats(matchId);
        const groupedByTeam = response.data.reduce((acc, player) => {
          const teamId = player.teamId || 'unknown';
          if (!acc[teamId]) {
            acc[teamId] = [];
          }
          acc[teamId].push(player);
          return acc;
        }, {});

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

  const handleAnalysis = async () => {
    if (!matchStats) return;
    setLoadingAnalysis(true);
    setAnalysis('');
    try {
        // 분석을 위해 모든 플레이어 스탯 데이터를 문자열로 변환
        const statsString = Object.values(matchStats).flat().map(player => (
            `- 플레이어: ${player.name}, 킬: ${player.kills}, 어시스트: ${player.assists}, 데미지: ${Math.round(player.damageDealt)}, 생존시간: ${Math.round(player.timeSurvived / 60)}분, 등수: ${player.winPlace}등`
        )).join('\n');
        
        const response = await getMatchAnalysis(statsString);
        setAnalysis(response.data);
    } catch (error) {
        console.error('Error fetching match analysis:', error);
        setAnalysis('분석 중 오류가 발생했습니다.');
    } finally {
        setLoadingAnalysis(false);
    }
  };

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
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
        <Typography variant="h4" component="h1">
          매치 상세 정보
        </Typography>
        <Button variant="outlined" onClick={() => navigate(`/players/${nickname}`)}>
          플레이어 정보로 돌아가기
        </Button>
      </Box>

      <Box mb={4}>
        <Button variant="contained" onClick={handleAnalysis} disabled={loadingAnalysis}>
          {loadingAnalysis ? <CircularProgress size={24} /> : 'AI 매치 분석'}
        </Button>
      </Box>

      {analysis && (
        <Card sx={{ mb: 4, whiteSpace: 'pre-wrap' }}>
          <CardHeader title="AI 분석 결과" />
          <CardContent>
            <Typography variant="body1">{analysis}</Typography>
          </CardContent>
        </Card>
      )}

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
