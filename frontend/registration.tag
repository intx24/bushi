<registration>
  <form action="#" onsubmit={ submit }>
    <div class="row">
      <label for="triggerInput">Trigger (required)
        <span class="error_message"> { triggerError } </span>
      </label>
      <input class="u-full-width" type="text" id="triggerInput"
             ref="trigger" oninput={ validateTrigger } required />
    </div>

    <div class="row">
      <label for="responseInput">Response text (required)
        <span class="error_message"> { responseError } </span>
      </label>
      <textarea class="u-full-width" id="responseInput"
                ref="response" oninput={ validateResponse } required></textarea>
    </div>

    <div class="row">
      <label for="nameInput">Name (optional) </label>
      <input class="u-full-width" type="text" id="nameInput" ref="name" />
    </div>

    <div class="row">
      <label for="iconEmojiInput">Icon (optional)</label>
    </div>
    <div class="row">
      <div class="seven columns">
        <input class="u-full-width" type="text" id="iconEmojiInput" ref="emoji" />
      </div>
      <div class="five columns">
        <button class="u-full-width" type="button" disabled>Upload file</button>
      </div>
    </div>

    <div class="row">
        <input class="button-primary" type="submit" value="Submit"/>
    </div>
    <div class="row">
        <span class="error_message u-full-width">{ submitError }</span>
    </div>
  </form>

  <script>
   this.dataHandler = opts.handler

   this.triggerError = ''
   this.responseError = ''
   this.submitError = ''

   validateTrigger(e) {
       let isBlank = e.target.value.trim().length == 0
       let isExists = this.dataHandler
                          .triggers
                          .filter(t => t.trigger === e.target.value)
                          .length != 0
       if (isBlank) {
           this.triggerError = 'cannot set blank trigger'
       } else if (isExists) {
           this.triggerError = 'already exists'
       } else {
           this.triggerError = ''
       }
   }

   validateResponse(e) {
       let isBlank = e.target.value.trim().length == 0
       if (isBlank) {
           this.responseError = 'cannot set blank response'
       } else {
           this.responseError = ''
       }
   }

   clearForm() {
       this.refs.trigger.value = ''
       this.refs.emoji.value = ''
       this.refs.name.value = ''
       this.refs.response.value = ''
   }

   submit(e) {
       this.submitError = ''
       this.dataHandler.add(this.refs.trigger.value, {
           icon_emoji: this.refs.emoji.value,
           icon_url: '',
           name: this.refs.name.value,
           response_text: this.refs.response.value
       }, (error) => {
           if (error) {
               this.submitError = error
               this.update()
           } else {
               this.clearForm()
           }
       })
    }
  </script>

  <style>
   textarea { resize: none; }
   .error_message { color: red; }
  </style>
</registration>
