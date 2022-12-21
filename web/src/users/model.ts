export type UserId = string

export enum UserOrigin {
  ALIEN,
  TEMP,
  GOOGLE,
}

export interface User {
  id: UserId
  username: string
  online: boolean
}

export interface AuthUser extends User {
  origin: UserOrigin
}
