export interface Course {
  id: number
  courseName: string
  courseCredit: number
  lastOperationTime: string
  startTime: Date | null
  endTime: Date | null
}

export function isCourse(arg: any): arg is Course {
  return arg.id !== undefined
}
