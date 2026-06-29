export type CategoryViewModel = {
  categoryCode: string
  categoryName: string
  displayOrder: number
  isActive: boolean
  description: string | null
}

export type CsvValidationErrorViewModel = {
  rowNumber: number | null
  field: string
  code: string
  message: string
}

export type CategoryListResponse = {
  categories: CategoryViewModel[]
}

export type CategoryCsvUploadSuccessResponse = {
  success: true
  message: string
  updatedCount: number
}

export type CategoryCsvUploadErrorResponse = {
  success: false
  message: string
  errorCount: number
  errors: CsvValidationErrorViewModel[]
}

export type ErrorResponse = {
  code: string
  message: string
}
