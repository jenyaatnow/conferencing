import React from 'react'
import './App.css'
import {useStore} from 'effector-react'
import {$usersStore} from './users'
import {WS} from './ws'

function ConfApp() {

  const ws = WS

  const users = useStore($usersStore)

  return (
    <>
      Hi, mom, it's me!!!
      {users.map(u => <p key={u.id}>{u.id}</p>)}
      Users count: {users.size}
    </>
  );
}

export default ConfApp;
