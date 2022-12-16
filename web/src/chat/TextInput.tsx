import {TextField} from '@mui/material'
import React, {useState} from 'react'

interface TextInputProps {
  onPressEnter: (text: string) => void,
}

export const TextInput = (props: TextInputProps) => {
  const [textInput, setTextInput] = useState('');

  const handlePressEnter = (e: React.KeyboardEvent<HTMLDivElement>) => {
    if (e.code === 'Enter') {
      props.onPressEnter(textInput)
      setTextInput('')
    }
  }

  const handleInputChange = (newValue: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    setTextInput(newValue.target.value)
  }

  return (
    <TextField fullWidth
               size={'small'}
               autoComplete={'off'}
               value={textInput}
               onKeyDown={handlePressEnter}
               onChange={handleInputChange}
    />
  )
}
