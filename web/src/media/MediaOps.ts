export const getLocalStream = async () => {
  try {
    return await navigator.mediaDevices.getUserMedia({
      audio: true,
      video: true,
    })
  } catch (e) {
    console.error(`getUserMedia() error:`, JSON.stringify(e))
    throw e
  }
}
