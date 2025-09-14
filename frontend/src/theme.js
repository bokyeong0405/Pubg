import { createTheme } from '@mui/material/styles';

const theme = createTheme({
  palette: {
    mode: 'dark',
    primary: {
      main: '#f57c00',
    },
    background: {
      default: '#212121',
      paper: '#424242',
    },
  },
  typography: {
    h2: {
      fontWeight: 'bold',
    },
    h4: {
      fontWeight: 'bold',
    },
    h5: {
      fontWeight: 'bold',
    },
  },
});

export default theme;
