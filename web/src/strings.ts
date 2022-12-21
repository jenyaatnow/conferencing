import {UserId} from './users'

export const AuthPageStrings = {
  UsernamePrompt: 'Enter your name',
  LoginButton: 'Sign In',
}

export const ChatStrings = {
  Offline: 'offline',
  You: 'you',
  UnknownUser: (userId: UserId) => `Unknown user [${userId}]`
}

export const Validation = {
  RequiredField: 'Required field',
}
