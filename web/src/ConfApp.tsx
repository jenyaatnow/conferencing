import React from 'react'
import './App.css'
import {useStore} from 'effector-react'
import {$usersStore} from './users'
import {WS} from './ws'
import {ChatComponent} from './chat'

function ConfApp() {
  const ws = WS

  const users = useStore($usersStore)

  return (
    <>
      Hi, mom, it's me!!!
      {users.filter(u => u.online).map(u => <p key={u.id}>{u.id}</p>)}
      Users count: {users.size}
      <br/>
      <br/>
      <ChatComponent chatType='conf' currentUserId={'Hank'}/>
    </>
  );
}

export default ConfApp;
