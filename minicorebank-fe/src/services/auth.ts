import { http } from "./http"

export type TokenRes = {
  accessToken: string
  tokenType: string
  role: "EMPLOYEE" | "CUSTOMER" | string
  customerId: string | null
}

export type LoginReq = {
  username: string
  password: string
}

export type RegisterReq = {
  username: string
  password: string
  fullName: string
  phone: string
  email: string
  currency: string
}

export async function login(payload: LoginReq) {
  const { data } = await http.post<TokenRes>("/api/auth/login", payload)
  return data
}

export async function registerCustomer(payload: RegisterReq) {
  const { data } = await http.post<TokenRes>("/api/auth/register/customer", payload)
  return data
}

export async function registerEmployee(payload: RegisterReq) {
  const { data } = await http.post<TokenRes>("/api/auth/register/employee", payload)
  return data
}