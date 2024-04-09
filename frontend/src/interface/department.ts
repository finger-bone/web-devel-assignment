export interface Department {
  id: number
  departmentName: string
  lastOperationTime: string
}
export function isDepartment(arg: unknown): arg is Department {
  return (
    arg !== null &&
    typeof arg === 'object' &&
    'id' in arg &&
    typeof arg['id'] === 'number' &&
    'departmentName' in arg &&
    typeof arg['departmentName'] === 'string' &&
    'lastOperationTime' in arg &&
    typeof arg['lastOperationTime'] === 'string'
  )
}
