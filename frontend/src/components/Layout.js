import React from 'react';
import { AppBar, Toolbar, Typography, Container, Box } from '@mui/material';
import { Link } from 'react-router-dom';

const Layout = ({ children }) => {
  return (
    <Box
      sx={{
        minHeight: '100vh',
        background: 'linear-gradient(45deg, #212121 30%, #424242 90%)',
        color: 'white',
      }}
    >
      <AppBar position="static" color="transparent" elevation={0}>
        <Toolbar>
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            <Link to="/" style={{ textDecoration: 'none', color: 'inherit' }}>
              PUBG Stats
            </Link>
          </Typography>
        </Toolbar>
      </AppBar>
      <Container component="main" sx={{ py: 4 }}>
        {children}
      </Container>
    </Box>
  );
};

export default Layout;
