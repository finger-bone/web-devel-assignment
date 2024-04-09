export interface Employee {
  id: number
  name: string
  userName: string
  gender: string
  image: string
  position: string | null
  hireDate: Date | null
  lastOperationTime: string
  departmentId: number | null
}

export function isEmployee(arg: any): arg is Employee {
  return arg.id !== undefined
}
