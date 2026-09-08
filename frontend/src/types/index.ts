export interface User {
  id: string
  username: string
  token: string
}

export interface Message {
  id: string
  title: string
  body: string
  userId: string
  createdAt: string
  updatedAt: string
}

export interface AuthResponse {
  id: string
  username: string
  token: string
}

export interface MessagePayload {
  title: string
  body: string
}
