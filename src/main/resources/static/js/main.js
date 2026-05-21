
    function validateImages(input) {
    const files = input.files;
    const maxFiles = 4;
    const maxSizeMB = 4;

    if (files.length > maxFiles) {
    alert(`You can only upload up to ${maxFiles} images.`);
    input.value = ''; // clear input
    return;
}

    for (let i = 0; i < files.length; i++) {
    if (files[i].size > maxSizeMB * 1024 * 1024) {
    alert(`Each file must be less than ${maxSizeMB}MB.`);
    input.value = '';
    return;
}
}
}
