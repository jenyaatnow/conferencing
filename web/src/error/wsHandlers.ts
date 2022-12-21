import {reportAppErrorFx} from './store'
import {ErrorWsMessage} from '../ws'

export const handleError = (e: ErrorWsMessage) => {
  reportAppErrorFx(e)
}
