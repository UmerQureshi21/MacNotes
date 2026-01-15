/**
 * This is the js file of the help page
 * Authors: Alhan Walli & Aryan Minhas
 */
document.addEventListener("DOMContentLoaded", () => {
  //FAQ ACCORDION CODE
  document.querySelectorAll('.faq-question').forEach(question => {
      // Add click event to each question
      question.addEventListener('click', () => {
          // Toggle the active class on the question
        question.classList.toggle('active');
        // Select the answer element
        let answer = question.nextElementSibling;
        // Toggle the active class on the answer
        answer.classList.toggle('active');
      });
    });
  });