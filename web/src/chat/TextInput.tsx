import {TextField} from '@mui/material'
import React, {useState} from 'react'

interface TextInputProps {
  onPressEnter: (text: string) => void,
}

export const TextInput = (props: TextInputProps) => {
  const [textInput, setTextInput] = useState('');

  const handleKeyDown = (e: React.KeyboardEvent<HTMLDivElement>) => {
    if (e.ctrlKey && e.code === 'Enter') {
      setTextInput(`${textInput}\n`)
      return
    }

    if (e.code === 'Enter') {
      e.preventDefault()
      if (!textInput.trim()) return
      props.onPressEnter(textInput)
      setTextInput('')
      return
    }
  }

  const handleInputChange = (newValue: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    setTextInput(newValue.target.value)
  }

  return (
    <TextField fullWidth
               size={'small'}
               autoComplete={'off'}
               multiline
               maxRows={4}
               value={textInput}
               onKeyDown={handleKeyDown}
               onChange={handleInputChange}
    />
  )
}
