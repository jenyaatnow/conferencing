import {Paper} from '@mui/material'
import {GlobalIndent} from '../globalStyles'

export const MainViewport = () => {
  return (
    <Paper variant={'outlined'} sx={{padding: GlobalIndent, height: '100%'}}>
      <div style={{background: 'grey', width: '100%', height: '100%'}}></div>
    </Paper>
  )
}
