import {createEffect, createEvent, createStore} from 'effector'
import {AppError} from './model'
import {identity} from '../utils'


export const reportAppErrorFx = createEffect(identity<AppError>)

export const discardAppErrorEvent = createEvent()

export const $appError = createStore<AppError | null>(null)
    .on(reportAppErrorFx.doneData, (_, data) => data)
    .on(discardAppErrorEvent, () => null)
