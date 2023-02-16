const videoList = document.querySelector('.video-list');
let loading = false;

function handleScroll() {
    const scrollTop = document.documentElement.scrollTop || document.body.scrollTop;
    const scrollHeight = document.documentElement.scrollHeight || document.body.scrollHeight;
    const clientHeight = document.documentElement.clientHeight || document.body.clientHeight;
    if (scrollTop + clientHeight >= scrollHeight - 100 && !loading) {
        loading = true;
        const videoContainer = document.createElement('div');
        videoContainer.classList.add('video-container');
        const videoPlayer = document.createElement('video');
        videoPlayer.classList.add('video-player');
        videoPlayer.src = 'video4.mp4';
        videoPlayer.controls = true;
        videoContainer.appendChild(videoPlayer);
        videoList.appendChild(videoContainer);
        const loadingSpinner = document.createElement('div');
        }
}

