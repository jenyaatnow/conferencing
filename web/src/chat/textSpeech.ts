import {User, UserId} from '../users'
import {List, Map} from 'immutable'
import {ChatMessageStoreEntry} from './model'
import {ChatStrings} from '../strings'


export interface TextSpeech {
  user: User
  messages: List<ChatMessageStoreEntry>
}

function prependMessage(speech: TextSpeech, message: ChatMessageStoreEntry): TextSpeech {
  return {...speech, messages: speech.messages.insert(0, message)}
}

function prependMessageToSpeech(speeches: List<TextSpeech>, message: ChatMessageStoreEntry, user: User): List<TextSpeech> {
  return speeches.isEmpty()
    ? prependSpeech(speeches, message, user)
    : speeches.rest().insert(0, prependMessage(speeches.first(), message))
}

function prependSpeech(speeches: List<TextSpeech>, message: ChatMessageStoreEntry, user: User): List<TextSpeech> {
  return speeches.insert(0, {user, messages: List.of(message)})
}

function prepend(speeches: List<TextSpeech>, message: ChatMessageStoreEntry, user: User): List<TextSpeech> {
  return speeches.first({user, messages: List.of(message)}).user.id === user.id
    ? prependMessageToSpeech(speeches, message, user)
    : prependSpeech(speeches, message, user)
}

export function splitSpeeches(messages: List<ChatMessageStoreEntry>, users: Map<UserId, User>): List<TextSpeech> {
  if (messages.isEmpty()) {
    return List()
  } else {
    const message = messages.first() as ChatMessageStoreEntry
    const user = users.get(message.message.from)
      || {id: message.message.from, username: ChatStrings.UnknownUser(message.message.from), online: false}

    return prepend(splitSpeeches(messages.rest(), users), message, user)
  }
}
