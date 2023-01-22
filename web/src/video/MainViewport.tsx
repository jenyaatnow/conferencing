import {MutableRefObject, useRef} from 'react'
import {getLocalStream} from '../media'
import {offer} from '../webrtc'

export const MainViewport = () => {
  const localVideo = useRef() as MutableRefObject<HTMLVideoElement>
  getLocalStream().then(localStream => {
    localVideo.current.srcObject = localStream
    offer(localStream)
  })

  return (
    <div>
      <video style={{width: '50vh', height: '50vh'}}
             ref={localVideo}
             autoPlay
             muted
      />
    </div>
  )
}
