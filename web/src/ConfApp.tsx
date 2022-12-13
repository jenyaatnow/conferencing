import React from 'react'
import './App.css'
import {WS} from './ws'
import {ChatComponent} from './chat'
import {Grid} from '@mui/material'
import {MainViewport} from './video/MainViewport'
import {GlobalIndent} from './globalStyles'

function ConfApp() {
  const ws = WS

  return (
    <Grid container spacing={GlobalIndent} sx={{padding: GlobalIndent, height: `${100-GlobalIndent}vh`}}>
      <Grid item xs={9}>
        <MainViewport/>
      </Grid>
      <Grid item xs={3}>
        <ChatComponent chatType='conf' currentUserId={'Hank'}/>
      </Grid>
    </Grid>
  );
}

export default ConfApp
