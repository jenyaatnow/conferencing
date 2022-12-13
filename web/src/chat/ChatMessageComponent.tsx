import {ChatMessage} from './model'
import {Grid, Typography} from '@mui/material'

interface ChatMessageComponentProps {
  message: ChatMessage
}

export const ChatMessageComponent = (props: ChatMessageComponentProps) => {
  return <Grid container spacing={1} wrap="nowrap">
    <Grid item>
      <Typography variant={'caption'} color={'grey'}>{props.message.timestamp.format('hh:mm')}</Typography>
    </Grid>
    <Grid item>
      <Typography>{props.message.text}</Typography>
    </Grid>
  </Grid>
}
