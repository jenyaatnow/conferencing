import {User, UserId} from '../users'
import {List, Map} from 'immutable'
import {ChatMessage} from './model'


export interface TextSpeech {
  user: User
  messages: List<ChatMessage>
}

function prependMessage(speech: TextSpeech, message: ChatMessage): TextSpeech {
  return {...speech, messages: speech.messages.insert(0, message)}
}

function prependMessageToSpeech(speeches: List<TextSpeech>, message: ChatMessage, user: User): List<TextSpeech> {
  return speeches.isEmpty()
    ? prependSpeech(speeches, message, user)
    : speeches.rest().insert(0, prependMessage(speeches.first(), message))
}

function prependSpeech(speeches: List<TextSpeech>, message: ChatMessage, user: User): List<TextSpeech> {
  return speeches.insert(0, {user, messages: List.of(message)})
}

function prepend(speeches: List<TextSpeech>, message: ChatMessage, user: User): List<TextSpeech> {
  return speeches.first({user, messages: List.of(message)}).user.id === user.id
    ? prependMessageToSpeech(speeches, message, user)
    : prependSpeech(speeches, message, user)
}

export function splitSpeeches(messages: List<ChatMessage>, users: Map<UserId, User>): List<TextSpeech> {
  if (messages.isEmpty()) {
    return List()
  } else {
    const message = messages.first() as ChatMessage
    const user = users.get(message.userId) || {id: message.userId, username: `Unknown user [${message.userId}]`, online: false}

    return prepend(splitSpeeches(messages.rest(), users), message, user)
  }
}
