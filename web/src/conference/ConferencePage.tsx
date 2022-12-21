import {wsConnectFx} from '../ws'
import {Grid} from '@mui/material'
import {GlobalIndent} from '../globalStyles'
import {MainViewport} from '../video'
import {ChatComponent, ChatTypes} from '../chat'
import React from 'react'
import {useStore} from 'effector-react'
import {$currentUserStore} from '../auth'

let shit = true

export const ConferencePage = () => {
  const authUser = useStore($currentUserStore)

  if (shit) { // todo fix this shit; useEffect calls twice
    // todo obtain conf id from url
    wsConnectFx({confId: '1', userId: authUser.id, username: authUser.username})
    shit = false
  }

  return (
    <Grid container spacing={GlobalIndent} sx={{padding: GlobalIndent, height: `${100-GlobalIndent}vh`, maxHeight: `${100-GlobalIndent}vh`}}>
      <Grid item xs={9} sx={{height: '100%'}}>
        <MainViewport/>
      </Grid>
      <Grid item xs={3} sx={{height: '100%'}}>
        <ChatComponent chatType={ChatTypes.Conf}/>
      </Grid>
    </Grid>
  )
}
