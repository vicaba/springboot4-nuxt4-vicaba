import type { paths } from '~/types/api'

export type ApiPaths = keyof paths

export type ApiResponse<
  P extends ApiPaths,
  M extends keyof paths[P] = 'get' extends keyof paths[P] ? 'get' : never,
> = paths[P][M] extends {
  responses: { 200: { content: { 'application/json': infer R } } }
}
  ? R
  : never

export function fetchApi<P extends ApiPaths>(
  path: P,
  options?: Parameters<typeof $fetch>[1],
): Promise<ApiResponse<P>> {
  return $fetch<ApiResponse<P>>(path, options)
}
