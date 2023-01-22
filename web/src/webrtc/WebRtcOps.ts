import {buildWebRtcAnswer, buildWebRtcOffer, wsSendFx} from '../ws'

const config = { iceServers: [{ urls: ['stun:stun.l.google.com:19302'] }] }

export const offer = async (localStream: MediaStream) => {
  const rtc = new RTCPeerConnection(config)
  localStream.getTracks().forEach(track => rtc.addTrack(track, localStream))

  const offer = await rtc.createOffer()
  await rtc.setLocalDescription(offer)

  await wsSendFx(buildWebRtcOffer(offer))
}

export const answer = async (offer: RTCSessionDescriptionInit, localStream: MediaStream) => {
  const rtc = new RTCPeerConnection(config)
  localStream.getTracks().forEach(track => rtc.addTrack(track, localStream))

  const sessionDescription = new RTCSessionDescription(offer) // todo handle deprecation
  await rtc.setRemoteDescription(sessionDescription)

  const answer = await rtc.createAnswer()
  await rtc.setLocalDescription(answer)

  await wsSendFx(buildWebRtcAnswer(answer))

  rtc.ontrack = ({ streams }) => handleRemoteStreams(streams)
}

export const handleAnswer = async (answer: RTCSessionDescriptionInit, rtc: RTCPeerConnection) => {
  const sessionDescription = new RTCSessionDescription(answer) // todo handle deprecation
  await rtc.setRemoteDescription(sessionDescription)

  rtc.ontrack = ({ streams }) => handleRemoteStreams(streams)
}

export const handleRemoteStreams = (streams: readonly MediaStream[]) => {}
