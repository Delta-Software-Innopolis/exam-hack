import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'

const push = vi.fn()

vi.mock('vue-router', () => ({
  useRouter: () => ({
    push,
  }),
}))

vi.mock('@/assets/Play.svg', () => ({
  default: {
    template: '<svg />',
  },
}))

import QuizComponent from './QuizComponent.vue'

describe('QuizComponent', () => {
  it('renders quiz name', () => {
    const wrapper = mount(QuizComponent, {
      props: {
        id: 1,
        name: 'Math quiz',
      },
    })

    expect(wrapper.text()).toContain('Math quiz')
  })

  it('shows "You" when author is not provided', () => {
    const wrapper = mount(QuizComponent, {
      props: {
        id: 1,
        name: 'Math quiz',
      },
    })

    expect(wrapper.text()).toContain('by You')
  })

  it('renders author name', () => {
    const wrapper = mount(QuizComponent, {
      props: {
        id: 1,
        name: 'Math quiz',
        author: 'Temuro',
      },
    })

    expect(wrapper.text()).toContain('by Temuro')
  })

  it('uses white variant by default', () => {
    const wrapper = mount(QuizComponent, {
      props: {
        id: 1,
        name: 'Math quiz',
      },
    })

    expect(wrapper.find('.quiz-item').classes()).toContain('white')
  })

  it('applies blueish variant', () => {
    const wrapper = mount(QuizComponent, {
      props: {
        id: 1,
        name: 'Math quiz',
        variant: 'blueish',
      },
    })

    expect(wrapper.find('.quiz-item').classes()).toContain('blueish')
  })

  it('falls back to blueish for invalid variant', () => {
    const wrapper = mount(QuizComponent, {
      props: {
        id: 1,
        name: 'Math quiz',
        variant: 'watermelon',
      },
    })

    expect(wrapper.find('.quiz-item').classes()).toContain('blueish')
  }),
  it('navigates to quiz page when header is clicked', async () => {
    push.mockClear()

    const wrapper = mount(QuizComponent, {
      props: {
        id: 42,
        name: 'Math quiz',
      },
    })

    await wrapper.find('.header').trigger('click')

    expect(push).toHaveBeenCalledWith({
      name: 'quiz',
      params: {
        quizId: 42,
      },
    })
  })
})
