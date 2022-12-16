export function identity<T>(t: T): T {
  return t
}

export function deepEq(a: any, b: any): boolean {
  return JSON.stringify(a) === JSON.stringify(b)
}
