export interface Class {
  id: number
  className: string
  classroom: string
  startTime: string
  endTime: string
  headTeacherId: number
}

export function isClass(arg: any): arg is Class {
  return (
    arg.id !== undefined &&
    arg.className !== undefined &&
    arg.classroom !== undefined &&
    arg.startTime !== undefined &&
    arg.endTime !== undefined &&
    arg.headTeacherId !== undefined
  )
}
