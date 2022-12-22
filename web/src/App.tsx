import React from 'react'
import {$currentUserStore, AuthPage} from './auth'
import {UserOrigin} from './users'
import {ConferencePage} from './conference'
import {useStore} from 'effector-react'
import {ErrorNotification} from './error'
import {BrowserRouter, Route, Routes} from 'react-router-dom'

function App() {
  const authUser = useStore($currentUserStore)
  const isAlien = authUser.origin === UserOrigin.ALIEN

  return <BrowserRouter>
    <ErrorNotification/>
    <Routes>
      <Route path="/:conferenceId" element={isAlien ? <AuthPage/> : <ConferencePage/>}/>
    </Routes>

  </BrowserRouter>
}

export default App
