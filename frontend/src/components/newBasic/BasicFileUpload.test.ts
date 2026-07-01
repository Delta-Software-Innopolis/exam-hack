import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import { defineComponent } from 'vue'
import { vi } from 'vitest'

vi.mock('@/assets/FileAdd.svg', () => ({
  default: defineComponent({
    template: '<svg />'
  })
}))

import BasicFileUpload from './BasicFileUpload.vue'

describe('BasicFileUpload', () => {
  it('shows placeholder when there are no files', () => {
    const wrapper = mount(BasicFileUpload)

    expect(wrapper.text()).toContain('Upload files first')
    expect(wrapper.find('ul').exists()).toBe(false)
  }),
  it('adds selected file to the list', async () => {
    const wrapper = mount(BasicFileUpload)

    const file = new File(['hello'], 'test.txt', {
      type: 'text/plain',
    })

    const input = wrapper.find('input')

    Object.defineProperty(input.element, 'files', {
      value: [file],
      writable: false,
    })

    await input.trigger('change')

    expect(wrapper.text()).toContain('test.txt')
  }),
  it('emits changed event after selecting a file', async () => {
    const wrapper = mount(BasicFileUpload)

    const file = new File(['hello'], 'test.txt', {
      type: 'text/plain',
    })

    const input = wrapper.find('input')

    Object.defineProperty(input.element, 'files', {
      value: [file],
      writable: false,
    })

    await input.trigger('change')

    expect(wrapper.emitted('changed')).toBeTruthy()
    expect(wrapper.emitted('changed')![0][0]).toHaveLength(1)
  }),
  it('removes file when remove button is clicked', async () => {
    const wrapper = mount(BasicFileUpload)

    const file = new File(['hello'], 'test.txt', {
      type: 'text/plain',
    })

    const input = wrapper.find('input')

    Object.defineProperty(input.element, 'files', {
      value: [file],
      writable: false,
    })

    await input.trigger('change')

    await wrapper.find('.remove-button').trigger('click')

    expect(wrapper.text()).not.toContain('test.txt')
    expect(wrapper.text()).toContain('Upload files first')
  }),
  it('emits changed after removing a file', async () => {
    const wrapper = mount(BasicFileUpload)

    const file = new File(['hello'], 'test.txt', {
      type: 'text/plain',
    })

    const input = wrapper.find('input')

    Object.defineProperty(input.element, 'files', {
      value: [file],
      writable: false,
    })

    await input.trigger('change')

    wrapper.emitted().changed = []

    await wrapper.find('.remove-button').trigger('click')

    expect(wrapper.emitted('changed')).toHaveLength(1)
    expect(wrapper.emitted('changed')![0][0]).toHaveLength(0)
  })
})
