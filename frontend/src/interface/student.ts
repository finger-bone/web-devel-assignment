export interface Student {
  id: number
  name: string
  studentNumber: string
  classId: number
  gender: string
  phoneNumber: string
  highestEducation: string
  disciplinaryActions: number
  disciplinaryPoints: number
  lastOperationTime: Date
}

export function isStudent(arg: any): arg is Student {
  return (
    arg.id !== undefined &&
    arg.name !== undefined &&
    arg.studentNumber !== undefined &&
    arg.classId !== undefined &&
    arg.gender !== undefined &&
    arg.phoneNumber !== undefined &&
    arg.highestEducation !== undefined &&
    arg.disciplinaryActions !== undefined &&
    arg.disciplinaryPoints !== undefined &&
    arg.lastOperationTime !== undefined
  )
}
