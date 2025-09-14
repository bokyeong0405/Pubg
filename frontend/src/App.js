import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import { ThemeProvider } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import theme from './theme';
import Layout from './components/Layout';
import SearchPage from './pages/SearchPage';
import PlayerPage from './pages/PlayerPage';
import MatchPage from './pages/MatchPage';

function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Router>
        <Layout>
          <Routes>
            <Route path="/" element={<SearchPage />} />
            <Route path="/players/:nickname" element={<PlayerPage />} />
            <Route path="/matches/:matchId" element={<MatchPage />} />
          </Routes>
        </Layout>
      </Router>
    </ThemeProvider>
  );
}

export default App;