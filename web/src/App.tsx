import React from 'react'
import {WS} from './ws'
import {ChatComponent, ChatTypes} from './chat'
import {Grid} from '@mui/material'
import {MainViewport} from './video/MainViewport'
import {GlobalIndent} from './globalStyles'

function App() {
  const ws = WS

  return (
    <Grid container spacing={GlobalIndent} sx={{padding: GlobalIndent, height: `${100-GlobalIndent}vh`, maxHeight: `${100-GlobalIndent}vh`}}>
      <Grid item xs={9} sx={{height: '100%'}}>
        <MainViewport/>
      </Grid>
      <Grid item xs={3} sx={{height: '100%'}}>
        <ChatComponent chatType={ChatTypes.Conf}/>
      </Grid>
    </Grid>
  );
}

export default App
