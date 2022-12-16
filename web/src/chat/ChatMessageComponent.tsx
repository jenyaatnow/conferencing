import {ChatMessageStoreEntry} from './model'
import {Grid, Typography} from '@mui/material'
import moment from 'moment'
import {UTC_WITH_TZ} from '../utils'

interface ChatMessageComponentProps {
  message: ChatMessageStoreEntry
}

export const ChatMessageComponent = (props: ChatMessageComponentProps) => {
  const ts = props.message.message.timestamp

  return <Grid container spacing={1} wrap="nowrap">
    <Grid item xs={1.3}>{
      ts && <Typography variant={'caption'} color={'grey'}>{moment(ts, UTC_WITH_TZ).format('HH:mm')}</Typography>
    }</Grid>
    <Grid item xs>
      <Typography {...(props.message.delivered ? {} : {color: 'grey'})}>{props.message.message.text}</Typography>
    </Grid>
  </Grid>
}
