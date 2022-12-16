import {ChatMessageStoreEntry} from './model'
import {Grid, Typography} from '@mui/material'
import moment from 'moment'

interface ChatMessageComponentProps {
  message: ChatMessageStoreEntry
}

export const ChatMessageComponent = (props: ChatMessageComponentProps) => {
  // TODO handle correct timezone offset console.log(Intl.DateTimeFormat().resolvedOptions().timeZone)

  const ts = props.message.message.timestamp

  return <Grid container spacing={1} wrap="nowrap">
    <Grid item xs={1.3}>{
      ts && <Typography variant={'caption'} color={'grey'}>{moment(ts, moment.ISO_8601).format('hh:mm')}</Typography>
    }</Grid>
    <Grid item xs>
      <Typography {...(props.message.delivered ? {} : {color: 'grey'})}>{props.message.message.text}</Typography>
    </Grid>
  </Grid>
}
