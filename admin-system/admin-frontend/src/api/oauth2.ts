import request from '@/utils/request'
import type { Result } from '@/types/api'
import type { Oauth2ProviderInfo } from '@/types/user'

export const getOauth2AuthorizeUrl = (provider: string) => {
  return request.get<any, Result<{ authorizeUrl: string; state: string }>>(`/oauth2/authorize/${provider}`)
}

export const getAvailableProviders = () => {
  return request.get<any, Result<Oauth2ProviderInfo[]>>('/oauth2/providers')
}
