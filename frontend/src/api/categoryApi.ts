import type {
  CategoryCsvUploadErrorResponse,
  CategoryCsvUploadSuccessResponse,
  CategoryListResponse,
  ErrorResponse
} from '../types/category'

export class ApiError extends Error {
  constructor(
    message: string,
    readonly status: number,
    readonly body: unknown
  ) {
    super(message)
    this.name = 'ApiError'
  }
}

const apiBaseUrl = import.meta.env.VITE_API_BASE_URL ?? ''

export async function fetchCategories(): Promise<CategoryListResponse> {
  const response = await fetch(`${apiBaseUrl}/api/categories`, {
    headers: {
      Accept: 'application/json'
    }
  })

  if (!response.ok) {
    throw await toApiError(response)
  }

  return response.json() as Promise<CategoryListResponse>
}

export async function downloadCategoriesCsv(): Promise<Blob> {
  const response = await fetch(`${apiBaseUrl}/api/categories/csv`, {
    headers: {
      Accept: 'text/csv'
    }
  })

  if (!response.ok) {
    throw await toApiError(response)
  }

  return response.blob()
}

export async function uploadCategoriesCsv(
  file: File
): Promise<CategoryCsvUploadSuccessResponse> {
  const formData = new FormData()
  formData.append('file', file)

  const response = await fetch(`${apiBaseUrl}/api/categories/csv`, {
    method: 'POST',
    body: formData,
    headers: {
      Accept: 'application/json'
    }
  })

  if (!response.ok) {
    throw await toApiError(response)
  }

  return response.json() as Promise<CategoryCsvUploadSuccessResponse>
}

export function isCategoryCsvUploadErrorResponse(
  body: unknown
): body is CategoryCsvUploadErrorResponse {
  if (!body || typeof body !== 'object') {
    return false
  }

  const candidate = body as Partial<CategoryCsvUploadErrorResponse>
  return candidate.success === false && Array.isArray(candidate.errors)
}

async function toApiError(response: Response): Promise<ApiError> {
  const body = await parseErrorBody(response)
  const message =
    isErrorResponse(body) || isCategoryCsvUploadErrorResponse(body)
      ? body.message
      : response.statusText

  return new ApiError(message, response.status, body)
}

async function parseErrorBody(response: Response): Promise<unknown> {
  const contentType = response.headers.get('content-type') ?? ''

  if (contentType.includes('application/json')) {
    return response.json()
  }

  return response.text()
}

function isErrorResponse(body: unknown): body is ErrorResponse {
  if (!body || typeof body !== 'object') {
    return false
  }

  const candidate = body as Partial<ErrorResponse>
  return typeof candidate.code === 'string' && typeof candidate.message === 'string'
}
