import {ChatMessage} from './model'
import {Grid, Typography} from '@mui/material'
import moment from 'moment'

interface ChatMessageComponentProps {
  message: ChatMessage
}

export const ChatMessageComponent = (props: ChatMessageComponentProps) => {
  // TODO handle correct timezone offset console.log(Intl.DateTimeFormat().resolvedOptions().timeZone)
  return <Grid container spacing={1} wrap="nowrap">
    <Grid item>
      <Typography variant={'caption'} color={'grey'}>{
        (moment(props.message.timestamp, moment.ISO_8601) || moment()).format('hh:mm') // TODO add gap for absent ts instead of current time
      }</Typography>
    </Grid>
    <Grid item>
      <Typography>{props.message.text}</Typography>
    </Grid>
  </Grid>
}
