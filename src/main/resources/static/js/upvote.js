async function upvoteClickHandler(event) {
  event.preventDefault();

//Parsing the id through window.location
  const id = window.location.toString().split('/')[
    window.location.toString().split('/').length - 1
  ];

//Waits for the parsed id to fetch the upvotes route then send the location to the PUT request
  const response = await fetch('/posts/upvote', {
    method: 'PUT',
    body: JSON.stringify({
        postId: id
    }),
    headers: {
      'Content-Type': 'application/json'
    }
  });

  if (response.ok) {
    document.location.reload();
  } else {
    alert(response.statusText);
  }
}

document.querySelector('.upvote-btn').addEventListener('click', upvoteClickHandler);