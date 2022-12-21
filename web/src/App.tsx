import React from 'react'
import {$currentUserStore, AuthPage} from './auth'
import {UserOrigin} from './users'
import {ConferencePage} from './conference'
import {useStore} from 'effector-react'
import {ErrorNotification} from './error'

function App() {
  const authUser = useStore($currentUserStore)
  const isAlien = authUser.origin === UserOrigin.ALIEN

  return <>
    <ErrorNotification/>
    {isAlien ? <AuthPage/> : <ConferencePage/>}
  </>
}

export default App
