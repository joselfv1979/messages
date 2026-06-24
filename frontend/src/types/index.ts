export interface User {
  id: number
  username: string
  token: string
}

export interface Message {
  id: number
  title: string
  body: string
  userId: number
  createdAt: string
  updatedAt: string
}

export interface AuthResponse {
  id: number
  username: string
  token: string
}

export interface MessagePayload {
  title: string
  body: string
}
